// @ts-nocheck

interface FluidObject {
    fluid: Fluid,
    amount?: number,
    nbt?: any,
}

type FluidStack = Amounted<Fluid> | FluidObject | EmptyFluid;
