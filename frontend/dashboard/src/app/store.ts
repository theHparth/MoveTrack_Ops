import { configureStore } from "@reduxjs/toolkit";
import movesReducer from "../features/moves/movesSlice";
import claimsReducer from "../features/claims/claimsSlice";

export const store = configureStore({
  reducer: {
    moves: movesReducer,
    claims: claimsReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
