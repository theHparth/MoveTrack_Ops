import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import { Provider } from "react-redux";
import { configureStore } from "@reduxjs/toolkit";
import movesReducer from "../features/moves/movesSlice";
import claimsReducer from "../features/claims/claimsSlice";
import MovesPage from "./MovesPage";

function renderWithStore() {
  const store = configureStore({
    reducer: { moves: movesReducer, claims: claimsReducer },
  });
  return render(
    <Provider store={store}>
      <MovesPage />
    </Provider>,
  );
}

describe("MovesPage", () => {
  it("renders the move request form", () => {
    renderWithStore();
    expect(
      screen.getByPlaceholderText("Service member name"),
    ).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Submit" })).toBeInTheDocument();
  });
});
