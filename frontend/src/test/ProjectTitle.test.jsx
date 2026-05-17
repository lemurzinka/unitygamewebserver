import { render, screen } from "@testing-library/react";
import ProjectTitle from "../components/ProjectTitle";

test("renders project title with correct text and class", () => {
  render(<ProjectTitle />);
  const title = screen.getByText("BLOON");
  expect(title).toBeInTheDocument();
  expect(title).toHaveClass("project-title");
});
