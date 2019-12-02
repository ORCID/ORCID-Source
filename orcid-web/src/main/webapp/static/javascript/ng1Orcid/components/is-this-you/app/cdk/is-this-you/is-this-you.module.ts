import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { IsThisYouComponent } from './is-this-you.component'
import { MatButtonModule } from '@angular/material'

@NgModule({
  declarations: [IsThisYouComponent],
  imports: [CommonModule, MatButtonModule],
  exports: [IsThisYouComponent],
})
export class IsThisYouModule {}
