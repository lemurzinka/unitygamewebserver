import { render, screen, waitFor } from "@testing-library/react";
import Dashboard from "../components/Dashboard";

jest.mock("react-chartjs-2", () => ({
  Bar: () => <div data-testid="mock-chart" />,
}));

beforeEach(() => {
  global.fetch = jest.fn(() =>
    Promise.resolve({
      json: () =>
        Promise.resolve({
          labels: ["Day 1", "Day 2"],
          data: [5, 10],
        }),
    })
  );
});

afterEach(() => {
  jest.clearAllMocks();
});

test("shows loading text initially", async () => {
  render(<Dashboard />);
  await waitFor(() => {
    expect(screen.getByText(/Loading chart.../i)).toBeInTheDocument();
  });
});

test("renders chart after fetch", async () => {
  render(<Dashboard />);
  await waitFor(() => {
    expect(screen.getByText("📊 User Activity")).toBeInTheDocument();
  });
  expect(screen.getByTestId("mock-chart")).toBeInTheDocument();
  expect(fetch).toHaveBeenCalledWith(
    "https://unitygamewebserver.onrender.com/api/logs/logins"
  );
});
