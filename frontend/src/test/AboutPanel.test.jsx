import { render, screen, fireEvent } from "@testing-library/react";
import AboutPanel from "../components/AboutPanel";

jest.useFakeTimers();

test("renders About BLOON heading", () => {
  render(<AboutPanel onClose={jest.fn()} />);
  expect(screen.getByText(/About BLOON/i)).toBeInTheDocument();
});

test("renders description text", () => {
  render(<AboutPanel onClose={jest.fn()} />);
  expect(
    screen.getByText(/BLOON is more than a platform/i)
  ).toBeInTheDocument();
});

