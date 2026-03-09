import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'am-profilecomponent',
  standalone: true,
  imports: [CommonModule],
  template: '<div class="page"><h1>ProfileComponent</h1><p>Coming soon...</p></div>',
  styles: ['.page { max-width: 1200px; margin: 0 auto; padding: 32px 24px; }']
})
export class ProfileComponent {}
