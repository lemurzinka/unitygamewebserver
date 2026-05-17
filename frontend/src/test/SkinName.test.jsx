import { render, screen } from "@testing-library/react";
import SkinName from "../components/SkinName";

test("renders skin name with correct text and class", () => {
  render(<SkinName name="Dragon Slayer" />);
  const element = screen.getByText("Dragon Slayer");
  expect(element).toBeInTheDocument();
  expect(element).toHaveClass("skin-name");
});
