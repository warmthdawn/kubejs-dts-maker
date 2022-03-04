type ItemOfArray<T extends string[]> =  T[Extract<keyof T, number>];

type ItemOfRegistry<T extends Record<string, string[]>> = keyof T extends string ? `${keyof T}:${ItemOfArray<T[keyof T]>}` : never;

type Counted<T extends string> = T | `${number}x ${T}`
type Amounted<T extends string> = T | `${number} ${T}`
