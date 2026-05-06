import { Player } from "@lottiefiles/react-lottie-player";
import signAnim from "../assets/animations/rollBall.json";

export default function SignAnimation() {
  return (
    <Player
      autoplay
      loop
      src={signAnim}
      style={{ height: "100%", width: "100%" }}
    />
  );
}
