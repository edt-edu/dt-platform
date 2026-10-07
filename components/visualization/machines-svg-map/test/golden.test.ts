/**
 * Golden test suite for machines-svg-map.
 *
 * Compares the SVG output produced by the CLI (via SvgGenerator) against
 * a saved "golden" reference file.  If the golden file does not exist yet
 * the test creates it so that subsequent runs can verify against it.
 *
 * Usage:
 *   npx tsx test/golden.test.ts
 *
 * Or:
 *   npm test
 */

import { strict as assert } from 'node:assert';
import {
  readFileSync,
  writeFileSync,
  existsSync,
  mkdirSync,
} from 'node:fs';
import { resolve, dirname } from 'node:path';
import { fileURLToPath } from 'node:url';

// ── Paths ────────────────────────────────────────────────────────────────
const __dirname = dirname(fileURLToPath(import.meta.url));
const ROOT = resolve(__dirname, '..');
const CONFIG_PATH = resolve(ROOT, 'resources/machine_config.json');
const GOLDEN_PATH = resolve(ROOT, 'test/__snapshots__/machine_config.golden.svg');
const OUTPUT_PATH = resolve(ROOT, 'dist/.golden_test_output.svg');

// ── CLI args that `npm run generate` uses ────────────────────────────────
const CLI_ARGS = {
  addPositions: true,
  addDimensions: true,
  accessibleZoneOpacity: 0.5,
};

// ── Helpers ──────────────────────────────────────────────────────────────

/**
 * Normalise an SVG string so that only *semantic* differences are detected.
 *
 * svg.js / svgdom may emit elements in a slightly different order or with
 * minor whitespace variations between runs (e.g. attribute order).  This
 * normaliser:
 *   1. Trims leading / trailing whitespace.
 *   2. Collapses runs of whitespace inside attribute values.
 *
 * Adjust this function if you need stricter or looser comparison.
 */
function normalizeSvg(raw: string): string {
  let s = raw.trim();
  s = s.replace(/\s+/g, ' ');
  return s;
}

/**
 * Run the generator programmatically by importing SvgGenerator and calling
 * the same pipeline that cli.ts uses.
 */
async function runGenerator(): Promise<string> {
  const { SvgGenerator } = await import('../dist/modules/SvgGenerator.js');

  const generator = new SvgGenerator(
    CLI_ARGS.addPositions,
    CLI_ARGS.addDimensions,
    CLI_ARGS.accessibleZoneOpacity,
  );

  generator.generateFromConfigFile(CONFIG_PATH);

  const svgString = generator.svg.svg();
  const formatted = (generator as any).formatXml(svgString);

  writeFileSync(OUTPUT_PATH, formatted);

  return formatted;
}

// ── Test framework (minimal) ─────────────────────────────────────────────

let passed = 0;
let failed = 0;

function describe(label: string, fn: () => void | Promise<void>) {
  console.log(`\n  ${label}`);
  return fn();
}

/**
 * Runs a single test case.  The factory may return void (sync) or a
 * Promise<void> (async).  Any thrown error or rejected promise is a failure.
 * Wraps the result in Promise.resolve() so both sync throws and async rejections
 * are caught uniformly.
 */
function it(label: string, fn: () => void | Promise<void>): Promise<void> {
  return Promise.resolve(fn() as Promise<void>).then(
    () => {
      passed++;
      console.log(`    ✓ ${label}`);
    },
    (err: unknown) => {
      failed++;
      const message = err instanceof Error ? err.message : String(err);
      console.error(`    ✗ ${label}`);
      console.error(`      ${message}`);
    },
  );
}

// ── Tests ────────────────────────────────────────────────────────────────

async function main() {
  console.log('═══════════════════════════════════════════');
  console.log('  machines-svg-map  ·  golden test suite');
  console.log('═══════════════════════════════════════════');

  // ── Pre-flight: config file must exist ─────────────────────────────
  await describe('pre-flight', async () => {
    await it('config file exists', () => {
      assert.ok(existsSync(CONFIG_PATH), `Config not found: ${CONFIG_PATH}`);
    });
  });

  // ── Run the generator ──────────────────────────────────────────────
  let generated: string;

  await describe('generate SVG', async () => {
    await it('runs without throwing', async () => {
      generated = await runGenerator();
      assert.ok(generated.length > 0, 'Generated SVG is empty');
    });

    await it('produces a valid SVG document', () => {
      assert.ok(
        generated.includes('<svg'),
        'Output does not contain an <svg> element',
      );
      assert.ok(
        generated.includes('</svg>'),
        'Output does not contain a closing </svg> tag',
      );
    });

    await it('contains expected machine IDs', () => {
      const ids = ['cb1', 'hbw1', 'slc1', 'mps1', 'vgr1', 'vgr2', 'table1'];
      for (const id of ids) {
        assert.ok(
          generated.includes(id),
          `Expected SVG to contain id="${id}" but it was not found`,
        );
      }
    });

    await it('contains expected component IDs', () => {
      const ids = ['sli1', 'sli2', 'sli3', 'sli4'];
      for (const id of ids) {
        assert.ok(
          generated.includes(id),
          `Expected SVG to contain id="${id}" but it was not found`,
        );
      }
    });
  });

  // ── Golden comparison ──────────────────────────────────────────────
  await describe('golden comparison', async () => {
    await it('creates golden file if it does not exist', async () => {
      if (!existsSync(GOLDEN_PATH)) {
        const goldenDir = dirname(GOLDEN_PATH);
        if (!existsSync(goldenDir)) {
          mkdirSync(goldenDir, { recursive: true });
        }
        writeFileSync(GOLDEN_PATH, generated, 'utf-8');
        console.log(`    → Golden file created at ${GOLDEN_PATH}`);
      }
    });

    await it('matches the golden file', async () => {
      const golden = normalizeSvg(readFileSync(GOLDEN_PATH, 'utf-8'));
      const actual = normalizeSvg(generated);

      if (actual !== golden) {
        const diffPath = OUTPUT_PATH.replace('.svg', '.diff.svg');
        writeFileSync(diffPath, generated, 'utf-8');
        throw new Error(
          `SVG differs from golden file.\n` +
            `  Golden: ${GOLDEN_PATH}\n` +
            `  Actual: ${OUTPUT_PATH}\n` +
            `  Diff saved: ${diffPath}\n` +
            `  Golden length: ${golden.length} chars\n` +
            `  Actual length:  ${actual.length} chars`,
        );
      }
    });
  });

  // ── Summary ────────────────────────────────────────────────────────
  console.log('');
  console.log(`───────────────────────────────────────────`);
  console.log(`  ${passed} passed, ${failed} failed`);
  console.log(`───────────────────────────────────────────`);

  if (failed > 0) {
    console.log(
      '\nTo update the golden file, delete it and re-run the test:',
    );
    console.log(`  rm "${GOLDEN_PATH}" && npx tsx test/golden.test.ts`);
    process.exit(1);
  } else {
    console.log('\n  All golden tests passed ✓');
    process.exit(0);
  }
}

main().catch((err) => {
  console.error('Fatal:', err);
  process.exit(2);
});
