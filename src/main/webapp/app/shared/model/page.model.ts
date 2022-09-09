export interface IPage {
  id?: number;
  title?: string | null;
  content?: string | null;
}

export const defaultValue: Readonly<IPage> = {};
