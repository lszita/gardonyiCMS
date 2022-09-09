export interface IPost {
  id?: number;
  title?: string | null;
  content?: string | null;
  author?: string | null;
}

export const defaultValue: Readonly<IPost> = {};
