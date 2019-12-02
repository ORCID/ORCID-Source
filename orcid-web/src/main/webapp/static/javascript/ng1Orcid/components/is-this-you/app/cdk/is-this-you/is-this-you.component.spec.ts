import { async, ComponentFixture, TestBed } from '@angular/core/testing'

import { IsThisYouComponent } from './is-this-you.component'

describe('IsThisYouComponent', () => {
  let component: IsThisYouComponent
  let fixture: ComponentFixture<IsThisYouComponent>

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [IsThisYouComponent],
    }).compileComponents()
  }))

  beforeEach(() => {
    fixture = TestBed.createComponent(IsThisYouComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
