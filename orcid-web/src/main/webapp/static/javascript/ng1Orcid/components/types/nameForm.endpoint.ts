import { Visibility, Value } from './common.endpoint'

export interface NameForm {
  visibility: Visibility
  errors?: any[]
  givenNames?: Value
  familyName?: Value
  creditName?: Value
}
