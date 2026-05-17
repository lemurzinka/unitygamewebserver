import { render, screen, fireEvent } from "@testing-library/react";
import Footer from "../components/Footer";


jest.mock("../components/AboutPanel", () => ({ onClose }) => (
  <div data-testid="about-panel" onClick={onClose}>About Panel</div>
));
jest.mock("../components/TermsPanel", () => ({ onClose }) => (
  <div data-testid="terms-panel" onClick={onClose}>Terms Panel</div>
));
jest.mock("../components/PrivacyPanel", () => ({ onClose }) => (
  <div data-testid="privacy-panel" onClick={onClose}>Privacy Panel</div>
));

test("renders footer with logo and copy", () => {
  render(<Footer />);
  expect(screen.getByText("BLOON")).toBeInTheDocument();
  expect(screen.getByText(/© 2025 BLOON/i)).toBeInTheDocument();
});

test("opens AboutPanel when 'Our Story' clicked", () => {
  render(<Footer />);
  fireEvent.click(screen.getByText("✦ Our Story"));
  expect(screen.getByTestId("about-panel")).toBeInTheDocument();
});

test("opens TermsPanel when 'Rules of Play' clicked", () => {
  render(<Footer />);
  fireEvent.click(screen.getByText("⚡ Rules of Play"));
  expect(screen.getByTestId("terms-panel")).toBeInTheDocument();
});

test("opens PrivacyPanel when 'Your Space' clicked", () => {
  render(<Footer />);
  fireEvent.click(screen.getByText("🔒 Your Space"));
  expect(screen.getByTestId("privacy-panel")).toBeInTheDocument();
});

test("closes panel when onClose called", () => {
  render(<Footer />);
  fireEvent.click(screen.getByText("✦ Our Story"));
  const panel = screen.getByTestId("about-panel");
  fireEvent.click(panel); 
  expect(panel).not.toBeInTheDocument();
});
