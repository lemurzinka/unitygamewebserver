import { render, screen, fireEvent } from "@testing-library/react";
import Header from "../components/Header";


jest.mock("../components/DonateModal", () => ({ onClose }) => (
  <div data-testid="donate-modal" onClick={onClose}>Donate Modal</div>
));
jest.mock("../components/SignUpModal", () => ({ onClose }) => (
  <div data-testid="signup-modal" onClick={onClose}>SignUp Modal</div>
));
jest.mock("../components/SignInModal", () => ({ onClose }) => (
  <div data-testid="signin-modal" onClick={onClose}>SignIn Modal</div>
));
jest.mock("../components/SkinsModal", () => ({ onClose }) => (
  <div data-testid="skins-modal" onClick={onClose}>Skins Modal</div>
));

beforeEach(() => {
  localStorage.clear();
  delete window.location;
  window.location = { reload: jest.fn() };
});

test("renders sign up and sign in when no user", () => {
  render(<Header />);
  expect(screen.getByText("Sign up")).toBeInTheDocument();
  expect(screen.getByText("Sign in")).toBeInTheDocument();
});

test("renders username and logout when user exists", () => {
  localStorage.setItem("user", JSON.stringify({ username: "Denis", balance: 100 }));
  render(<Header />);
  expect(screen.getByText("Denis")).toBeInTheDocument();
  expect(screen.getByText("Logout")).toBeInTheDocument();
  expect(screen.getByText(/Balance: 100/)).toBeInTheDocument();
});

test("opens donate modal when Buy clicked", () => {
  render(<Header />);
  fireEvent.click(screen.getByText("Buy"));
  expect(screen.getByTestId("donate-modal")).toBeInTheDocument();
});

test("opens signup modal when Sign up clicked", () => {
  render(<Header />);
  fireEvent.click(screen.getByText("Sign up"));
  expect(screen.getByTestId("signup-modal")).toBeInTheDocument();
});

test("opens signin modal when Sign in clicked", () => {
  render(<Header />);
  fireEvent.click(screen.getByText("Sign in"));
  expect(screen.getByTestId("signin-modal")).toBeInTheDocument();
});

test("opens skins modal when user logged in", () => {
  localStorage.setItem("user", JSON.stringify({ username: "Denis" }));
  render(<Header />);
  fireEvent.click(screen.getByText("Skins"));
  expect(screen.getByTestId("skins-modal")).toBeInTheDocument();
});

test("shows authRequired modal when Skins clicked without user", () => {
  render(<Header />);
  fireEvent.click(screen.getByText("Skins"));
  expect(screen.getByText("Access denied")).toBeInTheDocument();
});

test("logout clears localStorage and reloads page", () => {
  localStorage.setItem("user", JSON.stringify({ username: "Denis" }));
  render(<Header />);
  fireEvent.click(screen.getByText("Logout"));
  expect(localStorage.getItem("user")).toBeNull();
  expect(window.location.reload).toHaveBeenCalled();
});
