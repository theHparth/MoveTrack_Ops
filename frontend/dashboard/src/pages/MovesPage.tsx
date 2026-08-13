import { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import type { AppDispatch, RootState } from "../app/store";
import { submitMoveRequest } from "../features/moves/movesSlice";

function MovesPage() {
  const dispatch = useDispatch<AppDispatch>();
  const { current, status, error } = useSelector(
    (state: RootState) => state.moves,
  );

  const [serviceMemberName, setServiceMemberName] = useState("");
  const [rank, setRank] = useState("E5");
  const [originBase, setOriginBase] = useState("");
  const [destinationBase, setDestinationBase] = useState("");
  const [moveDate, setMoveDate] = useState("");

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    dispatch(
      submitMoveRequest({
        serviceMemberName,
        rank,
        originBase,
        destinationBase,
        moveDate,
      }),
    );
  };

  return (
    <section>
      <h1>Move Requests</h1>
      <form onSubmit={handleSubmit}>
        <input
          placeholder="Service member name"
          value={serviceMemberName}
          onChange={(e) => setServiceMemberName(e.target.value)}
          required
        />
        <select value={rank} onChange={(e) => setRank(e.target.value)}>
          {[
            "E1",
            "E2",
            "E3",
            "E4",
            "E5",
            "E6",
            "E7",
            "E8",
            "E9",
            "O1",
            "O2",
            "O3",
            "O4",
            "O5",
            "O6",
            "O7",
            "O8",
            "O9",
            "O10",
          ].map((r) => (
            <option key={r} value={r}>
              {r}
            </option>
          ))}
        </select>
        <input
          placeholder="Origin base"
          value={originBase}
          onChange={(e) => setOriginBase(e.target.value)}
          required
        />
        <input
          placeholder="Destination base"
          value={destinationBase}
          onChange={(e) => setDestinationBase(e.target.value)}
          required
        />
        <input
          type="date"
          value={moveDate}
          onChange={(e) => setMoveDate(e.target.value)}
          required
        />
        <button type="submit" disabled={status === "loading"}>
          Submit
        </button>
      </form>
      {status === "loading" && <p>Submitting...</p>}
      {error && <p role="alert">{error}</p>}
      {current && <h2>Entitlement: {current.entitlementLbs} lbs</h2>}
    </section>
  );
}

export default MovesPage;
