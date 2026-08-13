import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { moveEntitlementApi } from "../../api/client";

export interface MoveRequestPayload {
  serviceMemberName: string;
  rank: string;
  originBase: string;
  destinationBase: string;
  moveDate: string;
}

export interface MoveRequestResponse extends MoveRequestPayload {
  id: number;
  entitlementLbs: number;
}

interface MovesState {
  current: MoveRequestResponse | null;
  status: "idle" | "loading" | "succeeded" | "failed";
  error: string | null;
}

const initialState: MovesState = { current: null, status: "idle", error: null };

export const submitMoveRequest = createAsyncThunk(
  "moves/submit",
  (payload: MoveRequestPayload) =>
    moveEntitlementApi.submitMoveRequest<MoveRequestResponse>(payload),
);

const movesSlice = createSlice({
  name: "moves",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(submitMoveRequest.pending, (state) => {
        state.status = "loading";
        state.error = null;
      })
      .addCase(submitMoveRequest.fulfilled, (state, action) => {
        state.status = "succeeded";
        state.current = action.payload;
      })
      .addCase(submitMoveRequest.rejected, (state, action) => {
        state.status = "failed";
        state.error = action.error.message ?? "Unknown error";
      });
  },
});

export default movesSlice.reducer;
