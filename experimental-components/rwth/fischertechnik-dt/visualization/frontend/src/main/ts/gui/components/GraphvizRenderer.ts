import { Component, Input, ViewChild, ElementRef, OnChanges, SimpleChanges } from '@angular/core';
import { ComponentObserverManager } from '@umlp/commonj2ts';
import { GraphvizRendererComponent, config } from 'components/GraphvizRendererComponent';
import { OnDestroy } from '@angular/core';
import { instance, Viz } from "@viz-js/viz";
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: [...config.styleUrls]
})
export class GraphvizRenderer extends GraphvizRendererComponent implements OnDestroy {

  @ViewChild('graphContainer', { static: true })
  public graphContainer!: ElementRef;

  public graphSvg: SafeHtml | null = null;
  private viz: Viz | null = null;

  constructor(private sanitizer: DomSanitizer) {
    super();
    instance().then(viz => {
      this.viz = viz;
      if (this.dotFileContent) {
        this.renderGraph();
      }
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['dotFileContent'] && this.viz) {
      this.renderGraph();
    }
  }

  private renderGraph(): void {
    if (!this.dotFileContent || !this.viz) {
      this.graphSvg = null;
      return;
    }
    try {
      const svgElement = this.viz.renderSVGElement(this.dotFileContent);
      this.graphSvg = this.sanitizer.bypassSecurityTrustHtml(svgElement.outerHTML);
    } catch (error) {
      console.error('Error rendering graph:', error);
      this.graphSvg = 'Error rendering graph. Please check the console for details.';
    }
  }

  public ngOnDestroy(): void {
    super.ngOnDestroy();
    ComponentObserverManager.removeObservers("components.GraphvizRenderer" + this.uniqueIdentifier);
  }
}