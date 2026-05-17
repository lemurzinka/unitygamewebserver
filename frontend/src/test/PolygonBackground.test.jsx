import { render, screen } from "@testing-library/react";
import PolygonBackground from "../components/PolygonBackground";

test("renders polygon background image with correct alt and class", () => {
  render(<PolygonBackground />);
  const img = screen.getByAltText("Polygon background");
  expect(img).toBeInTheDocument();
  expect(img).toHaveClass("polygon-bg");
});
