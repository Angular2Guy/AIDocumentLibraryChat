import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DocSearchComponent } from './doc-search.component';

describe('DocSearchComponent', () => {
  let component: DocSearchComponent;
  let fixture: ComponentFixture<DocSearchComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DocSearchComponent]
    });
    fixture = TestBed.createComponent(DocSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
