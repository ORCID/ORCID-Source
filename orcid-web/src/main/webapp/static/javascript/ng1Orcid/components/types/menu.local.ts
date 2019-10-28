export interface ApplicationMenuItem {
  id: string
  label: string
  route?: string
  hover: boolean
  active: boolean
  buttons?: ApplicationMenuItem[]
  activeRoute?: string
  requirements?: MenuItemRequirement
}

export interface ApplicationMenuItemBasic {
  id: string
  label: string
  route?: string
  buttons?: ApplicationMenuItemBasic[]
  activeRoute?: string
  requirements?: MenuItemRequirement
}

export interface MenuItemRequirement {
  logging?: boolean
  desktop?: boolean
  requiresAll?: Requirement[]
  requiresAny?: Requirement[]
  togglz?: Requirement[]
}

export interface Requirement {
  [key: string]: string
}
