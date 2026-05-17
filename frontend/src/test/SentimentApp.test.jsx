import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import SentimentApp from "../components/SentimentApp";

jest.useFakeTimers();

beforeEach(() => {
  global.fetch = jest.fn();
});

afterEach(() => {
  jest.clearAllMocks();
});

test("renders sentiment app with title and textarea", () => {
  render(<SentimentApp />);
  expect(screen.getByText("Send feedback")).toBeInTheDocument();
  expect(screen.getByPlaceholderText("Type something...")).toBeInTheDocument();
  expect(screen.getByText("Analyze")).toBeInTheDocument();
});

test("updates textarea value on input", () => {
  render(<SentimentApp />);
  const textarea = screen.getByPlaceholderText("Type something...");
  fireEvent.change(textarea, { target: { value: "Hello world" } });
  expect(textarea.value).toBe("Hello world");
});

test("shows positive result after fetch", async () => {
  global.fetch.mockResolvedValueOnce({
    ok: true,
    json: () => Promise.resolve({ label: "POSITIVE" }),
  });

  render(<SentimentApp />);
  fireEvent.change(screen.getByPlaceholderText("Type something..."), {
    target: { value: "Great job!" },
  });
  fireEvent.click(screen.getByText("Analyze"));

  await waitFor(() => {
    expect(screen.getByText("😊 Thank you for your feedback!")).toBeInTheDocument();
  });
});

test("shows negative result after fetch", async () => {
  global.fetch.mockResolvedValueOnce({
    ok: true,
    json: () => Promise.resolve({ label: "NEGATIVE" }),
  });

  render(<SentimentApp />);
  fireEvent.change(screen.getByPlaceholderText("Type something..."), {
    target: { value: "Bad experience" },
  });
  fireEvent.click(screen.getByText("Analyze"));

  await waitFor(() => {
    expect(screen.getByText("🙏 We have taken your wishes")).toBeInTheDocument();
  });
});

