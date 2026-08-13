import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { claimsApi } from "../../api/client";

export interface ClaimPayload {
  moveRequestId: number;
  claimantName: string;
  description: string;
  claimedAmount: number;
}

export interface ClaimResponse extends ClaimPayload {
  id: number;
  status: string;
  filedDate: string;
}

interface ClaimsState {
  current: ClaimResponse | null;
  status: "idle" | "loading" | "succeeded" | "failed";
  error: string | null;
}

const initialState: ClaimsState = {
  current: null,
  status: "idle",
  error: null,
};

export const fileClaim = createAsyncThunk(
  "claims/file",
  (payload: ClaimPayload) => claimsApi.fileClaim<ClaimResponse>(payload),
);

const claimsSlice = createSlice({
  name: "claims",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fileClaim.pending, (state) => {
        state.status = "loading";
        state.error = null;
      })
      .addCase(fileClaim.fulfilled, (state, action) => {
        state.status = "succeeded";
        state.current = action.payload;
      })
      .addCase(fileClaim.rejected, (state, action) => {
        state.status = "failed";
        state.error = action.error.message ?? "Unknown error";
      });
  },
});

export default claimsSlice.reducer;
