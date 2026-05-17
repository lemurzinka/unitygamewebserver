import { render, screen } from "@testing-library/react";
import CurrentSkin from "../components/CurrentSkin";

test("renders current skin text", () => {
  render(<CurrentSkin />);
  expect(screen.getByText(/YOUR CURRENT SKIN IS:/i)).toBeInTheDocument();
});
