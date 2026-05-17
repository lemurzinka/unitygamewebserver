import { render, screen,} from "@testing-library/react";
import TermsPanel from "../components/TermsPanel";

jest.useFakeTimers();

test("renders terms panel with title and text", () => {
  render(<TermsPanel onClose={() => {}} />);
  expect(screen.getByText("Rules of Play")).toBeInTheDocument();
  expect(
    screen.getByText(/BLOON is a playground, not a battlefield/i)
  ).toBeInTheDocument();
});


