import { render, screen, fireEvent } from "@testing-library/react";
import PrivacyPanel from "../components/PrivacyPanel";

jest.useFakeTimers();

test("renders privacy panel with title and text", () => {
  render(<PrivacyPanel onClose={() => {}} />);
  expect(screen.getByText("Your Space")).toBeInTheDocument();
  expect(
    screen.getByText(/Your data is yours. BLOON only keeps/i)
  ).toBeInTheDocument();
});

