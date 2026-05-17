import { render, screen } from "@testing-library/react";
import Polygon from "../components/Polygon";

test("renders polygon image with correct alt and class", () => {
  render(<Polygon />);
  const img = screen.getByAltText("Polygon");
  expect(img).toBeInTheDocument();
  expect(img).toHaveClass("polygon");
});