import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import DonateModal from "../components/DonateModal";

jest.mock("framer-motion", () => ({
  motion: {
    div: ({ children, ...props }) => <div {...props}>{children}</div>,
  },
}));

beforeEach(() => {
  global.fetch = jest.fn();
  jest.spyOn(window, "alert").mockImplementation(() => {});
  delete window.location;
  window.location = { href: "" };
  localStorage.clear();
});

afterEach(() => {
  jest.clearAllMocks();
});

test("shows alert if user not logged in", async () => {
  render(<DonateModal onClose={() => {}} />);
  const buyButton = screen.getAllByText("BUY")[0];
  fireEvent.click(buyButton);

  await waitFor(() => {
    expect(window.alert).toHaveBeenCalledWith(
      "You must be logged in to make a purchase."
    );
  });
});

test("calls fetch with correct priceId when logged in", async () => {
  localStorage.setItem("user", JSON.stringify({ token: "fake-token" }));
  global.fetch.mockResolvedValueOnce({
    ok: true,
    json: () => Promise.resolve({ url: "https://stripe.com/session" }),
  });

  render(<DonateModal onClose={() => {}} />);
  const buyButton = screen.getAllByText("BUY")[0];
  fireEvent.click(buyButton);

  await waitFor(() => {
    expect(fetch).toHaveBeenCalledWith(
      "https://unitygamewebserver.onrender.com/stripe/create-checkout-session",
      expect.objectContaining({
        method: "POST",
        headers: expect.objectContaining({
          Authorization: "Bearer fake-token",
        }),
        body: JSON.stringify({ priceId: "price_1SI4ngFQqejofOUdvOz1Z8TU" }),
      })
    );
  });

 
  expect(window.location.href).toBe("https://stripe.com/session");
});


test("shows alert if backend returns no url", async () => {
  localStorage.setItem("user", JSON.stringify({ token: "fake-token" }));
  global.fetch.mockResolvedValueOnce({
    ok: true,
    json: () => Promise.resolve({}),
  });

  render(<DonateModal onClose={() => {}} />);
  const buyButton = screen.getAllByText("BUY")[0];
  fireEvent.click(buyButton);

  await waitFor(() => {
    expect(window.alert).toHaveBeenCalledWith("No redirect URL received.");
  });
});

test("shows alert if fetch fails", async () => {
  localStorage.setItem("user", JSON.stringify({ token: "fake-token" }));
  global.fetch.mockRejectedValueOnce(new Error("Network error"));

  render(<DonateModal onClose={() => {}} />);
  const buyButton = screen.getAllByText("BUY")[0];
  fireEvent.click(buyButton);

  await waitFor(() => {
    expect(window.alert).toHaveBeenCalledWith("Unexpected error occurred.");
  });
});
