import { render, screen, waitFor } from "@testing-library/react";
import SuccessPage from "../components/SuccessPage";

beforeEach(() => {
  jest.spyOn(window, "fetch").mockImplementation(() =>
    Promise.resolve({
      ok: true,
      json: () => Promise.resolve({ balance: 100 }),
    })
  );
  delete window.location;
  window.location = { href: "/" };
  localStorage.clear();
});

afterEach(() => {
  jest.clearAllMocks();
});

test("renders processing message", () => {
  render(<SuccessPage />);
  expect(screen.getByText("Processing payment...")).toBeInTheDocument();
});

test("redirects if no user in localStorage", () => {
  render(<SuccessPage />);
  expect(window.location.href).toBe("/");
});


test("redirects on fetch error", async () => {
  window.fetch.mockImplementationOnce(() => Promise.reject(new Error("Network error")));
  localStorage.setItem("user", JSON.stringify({ id: 1, token: "abc123" }));

  render(<SuccessPage />);

  await waitFor(() => {
    expect(window.location.href).toBe("/");
  });
});
