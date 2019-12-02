import { NgModule } from '@angular/core'
import { CommonModule } from '@angular/common'
import { PlatformInfoService } from './platform-info.service'
import { PlatformModule } from '@angular/cdk/platform'
import { LayoutModule } from '@angular/cdk/layout'

@NgModule({
  declarations: [],
  imports: [CommonModule, PlatformModule, LayoutModule],
  providers: [PlatformInfoService],
})
export class PlatformInfoModule {}
