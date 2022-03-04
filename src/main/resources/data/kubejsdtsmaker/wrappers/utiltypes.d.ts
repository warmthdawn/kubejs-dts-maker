type ItemOfArray<T extends string[]> = keyof T extends number ? T[keyof T] : never;

type ItemOfRegistry<T extends Record<string, string[]>> = keyof T extends string ? `${keyof T}:${ItemOfArray<T[keyof T]>}` : never;

type Counted<T extends string> = `${number}x ${T}`
type Amounted<T extends string> = `${number} ${T}`
