export interface Source {
    sourceOrcid?: SourceData;
    sourceClientId?: SourceData;
    sourceName?: Content;
    assertionOriginOrcid?: SourceData;
    assertionOriginClientId?: SourceData;
    assertionOriginName?: Content;
  }
  
  interface SourceData {
    uri: string;
    path: string;
    host: string;
  }
  
  interface Content {
    content: string;
  }
  