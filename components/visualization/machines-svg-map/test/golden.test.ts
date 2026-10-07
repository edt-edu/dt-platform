/**
 * Golden test suite for machines-svg-map.
 *
 * Compares the SVG output produced by the CLI (via SvgGenerator) against
 * a saved "golden" reference file.  If the golden file does not exist yet
 * the test creates it so that subsequent runs can verify against it.
 *
 * Usage:
 *   npx tsx test/golden.test.ts          # human-readable output
 *   npx tsx --test test/golden.test.ts   # node:test harness + reporters
 *   npm test                              # same as above via package.json
 *
 * JUnit XML is emitted automatically when run through the node:test runner:
 *   npx tsx --test --test-reporter=junit --test-reporter-destination=dist/junit.xml test/golden.test.ts
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
import { describe, it } from 'node:test';

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
 * Run the generator programmatically by importing the built SvgGenerator
 * and calling the same pipeline that cli.ts uses.
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

// ── Tests ────────────────────────────────────────────────────────────────

describe('golden test suite', () => {
  // ── Pre-flight: config file must exist ─────────────────────────────
  it('config file exists', () => {
    assert.ok(existsSync(CONFIG_PATH), `Config not found: ${CONFIG_PATH}`);
  });

  // ── Run the generator ──────────────────────────────────────────────
  let generated: string;

  it('generator runs without throwing', async () => {
    generated = await runGenerator();
    assert.ok(generated.length > 0, 'Generated SVG is empty');
  });

  it('produces a valid SVG document', () => {
    assert.ok(
      generated.includes('<svg'),
      'Output does not contain an <svg> element',
    );
    assert.ok(
      generated.includes('</svg>'),
      'Output does not contain a closing </svg> tag',
    );
  });

  it('contains expected machine IDs', () => {
    const ids = ['cb1', 'hbw1', 'slc1', 'mps1', 'vgr1', 'vgr2', 'table1'];
    for (const id of ids) {
      assert.ok(
        generated.includes(id),
        `Expected SVG to contain id="${id}" but it was not found`,
      );
    }
  });

  it('contains expected component IDs', () => {
    const ids = ['sli1', 'sli2', 'sli3', 'sli4'];
    for (const id of ids) {
      assert.ok(
        generated.includes(id),
        `Expected SVG to contain id="${id}" but it was not found`,
      );
    }
  });

  // ── Golden comparison ──────────────────────────────────────────────
  it('creates golden file if it does not exist', () => {
    if (!existsSync(GOLDEN_PATH)) {
      const goldenDir = dirname(GOLDEN_PATH);
      if (!existsSync(goldenDir)) {
        mkdirSync(goldenDir, { recursive: true });
      }
      writeFileSync(GOLDEN_PATH, generated, 'utf-8');
    }
  });

  it('matches the golden file', () => {
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
