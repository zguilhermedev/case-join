import type { AppProps } from "next/app";
import { AuthProvider } from "../contexts/AuthContext";
import Layout from "../components/layout";
import '../styles/globals.css';

export default function App({ Component, pageProps }: AppProps) {
  return (
    <AuthProvider>
      <Layout>
        <Component {...pageProps} />
      </Layout>
    </AuthProvider>
  );
}