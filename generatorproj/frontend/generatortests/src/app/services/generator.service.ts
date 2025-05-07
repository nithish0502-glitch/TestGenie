import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GeneratorService {
  private apiUrl = 'https://testgenie.onrender.com'; // Update if backend is deployed

  constructor(private http: HttpClient) {}

  // Generate suggestions for models, exceptions, and JPQL queries
  generateSuggestions(relationshipType: string, modelCount: number, regenerate: boolean = false): Observable<any> {
    const body = { relationshipType, modelCount, regenerate };
    return this.http.post<any>(`${this.apiUrl}/generate-suggestion`, body);
  }

  // Modify the test file based on user selection
  modifyTestFile(selectedModel: string, relationshipType: string, selectedException: string, selectedQuery: string): Observable<Blob> {
    const body = { selectedModel, relationshipType, selectedException, selectedQuery };
    
    return this.http.post(`${this.apiUrl}/modify-test-file`, body, {
      responseType: 'blob', // Expecting a zip file as response
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    });
  }

  // Download the modified test zip file
  downloadTestFile(modifiedBlob: Blob, fileName: string = 'springapptests.zip'): void {
    const url = window.URL.createObjectURL(modifiedBlob);
    const a = document.createElement('a');
    a.href = url;
    a.download = fileName;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
  }
}
