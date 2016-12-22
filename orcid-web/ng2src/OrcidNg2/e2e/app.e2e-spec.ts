import { OrcidNg2Page } from './app.po';

describe('orcid-ng2 App', function() {
  let page: OrcidNg2Page;

  beforeEach(() => {
    page = new OrcidNg2Page();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
