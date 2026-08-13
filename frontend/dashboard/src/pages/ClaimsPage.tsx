import { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import type { AppDispatch, RootState } from "../app/store";
import { fileClaim } from "../features/claims/claimsSlice";

function ClaimsPage() {
  const dispatch = useDispatch<AppDispatch>();
  const { current, status, error } = useSelector(
    (state: RootState) => state.claims,
  );

  const [moveRequestId, setMoveRequestId] = useState("");
  const [claimantName, setClaimantName] = useState("");
  const [description, setDescription] = useState("");
  const [claimedAmount, setClaimedAmount] = useState("");

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    dispatch(
      fileClaim({
        moveRequestId: Number(moveRequestId),
        claimantName,
        description,
        claimedAmount: Number(claimedAmount),
      }),
    );
  };

  return (
    <section>
      <h1>Claims</h1>
      <form onSubmit={handleSubmit}>
        <input
          placeholder="Move request ID"
          value={moveRequestId}
          onChange={(e) => setMoveRequestId(e.target.value)}
          required
        />
        <input
          placeholder="Claimant name"
          value={claimantName}
          onChange={(e) => setClaimantName(e.target.value)}
          required
        />
        <input
          placeholder="Description"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          required
        />
        <input
          type="number"
          placeholder="Claimed amount"
          value={claimedAmount}
          onChange={(e) => setClaimedAmount(e.target.value)}
          required
        />
        <button type="submit" disabled={status === "loading"}>
          File claim
        </button>
      </form>
      {status === "loading" && <p>Submitting...</p>}
      {error && <p role="alert">{error}</p>}
      {current && (
        <h2>
          Claim #{current.id} — {current.status}
        </h2>
      )}
    </section>
  );
}

export default ClaimsPage;
