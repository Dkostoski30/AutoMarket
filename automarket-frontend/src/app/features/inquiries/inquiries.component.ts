import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'am-inquiriescomponent',
  standalone: true,
  imports: [CommonModule],
  template: '<div class="page"><h1>InquiriesComponent</h1><p>Coming soon...</p></div>',
  styles: ['.page { max-width: 1200px; margin: 0 auto; padding: 32px 24px; }']
})
export class InquiriesComponent {}
