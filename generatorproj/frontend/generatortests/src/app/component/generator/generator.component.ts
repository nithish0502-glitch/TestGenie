import { Component, Renderer2 } from '@angular/core';
import { GeneratorService } from '../../services/generator.service';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-generator',
  templateUrl: './generator.component.html',
  styleUrls: ['./generator.component.css']
})
export class GeneratorComponent {
  relationshipType: string = 'OneToOne'; // Default relationship
  modelCount: number = 2; // Default model count
  suggestions: any = null;
  useManualInput = false; // Toggle state
  selectedModel: string = '';
  selectedException: string = '';
  selectedQuery: string = '';
  isLoading: boolean = false;

  constructor(private generatorService: GeneratorService, private renderer: Renderer2,private cdr: ChangeDetectorRef) {}

  // Toggle between dropdown and manual input
  toggleManualInput(): void {
    console.log("Toggled useManualInput:", this.useManualInput); // Debugging log
    this.useManualInput = !this.useManualInput;
    
    // Reset values when toggling mode
    this.selectedModel = '';
    this.selectedException = '';
    this.selectedQuery = '';
    this.cdr.detectChanges();
  }

  // Generate Suggestions
  generateSuggestions(): void {
    this.isLoading = true;
    this.generatorService.generateSuggestions(this.relationshipType, this.modelCount).subscribe(
      (data) => {
        this.suggestions = data;
        this.resetSelections();
        this.isLoading = false;
      },
      (error) => {
        console.error('Error generating suggestions:', error);
        this.isLoading = false;
      }
    );
  }

  // Reset selections when new suggestions are fetched
  private resetSelections(): void {
    this.selectedModel = '';
    this.selectedException = '';
    this.selectedQuery = '';
  }

  // Get the selected model's details safely
  getSelectedModel() {
    return this.suggestions?.suggestions?.find((s: any) => s.model === this.selectedModel) || null;
  }

  // Modify Test File and Download
  modifyTestFile(): void {
    if (!this.selectedModel || !this.selectedException || !this.selectedQuery) {
      alert('Please select all fields before modifying the test file.');
      return;
    }

    this.isLoading = true;
    this.generatorService.modifyTestFile(this.selectedModel, this.relationshipType, this.selectedException, this.selectedQuery)
      .subscribe(
        (blob) => {
          this.generatorService.downloadTestFile(blob);
          this.isLoading = false;
        },
        (error) => {
          console.error('Error modifying test file:', error);
          this.isLoading = false;
        }
      );
  }
}
