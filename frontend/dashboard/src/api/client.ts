const MOVE_ENTITLEMENT_BASE_URL = "http://localhost:8082/api";
const CLAIMS_BASE_URL = "http://localhost:8081/api";

async function request<T>(url: string, options?: RequestInit): Promise<T> {
  const response = await fetch(url, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });

  if (!response.ok) {
    throw new Error(
      `Request failed: ${response.status} ${response.statusText}`,
    );
  }

  return response.json() as Promise<T>;
}

export const moveEntitlementApi = {
  submitMoveRequest: <T>(body: unknown) =>
    request<T>(`${MOVE_ENTITLEMENT_BASE_URL}/move-requests`, {
      method: "POST",
      body: JSON.stringify(body),
    }),
};

export const claimsApi = {
  fileClaim: <T>(body: unknown) =>
    request<T>(`${CLAIMS_BASE_URL}/claims`, {
      method: "POST",
      body: JSON.stringify(body),
    }),
  getClaim: <T>(id: number) => request<T>(`${CLAIMS_BASE_URL}/claims/${id}`),
  adjusterDecision: <T>(id: number, approved: boolean) =>
    request<T>(`${CLAIMS_BASE_URL}/claims/${id}/adjuster-decision`, {
      method: "POST",
      body: JSON.stringify({ approved }),
    }),
  supervisorDecision: <T>(id: number, approved: boolean) =>
    request<T>(`${CLAIMS_BASE_URL}/claims/${id}/supervisor-decision`, {
      method: "POST",
      body: JSON.stringify({ approved }),
    }),
};
