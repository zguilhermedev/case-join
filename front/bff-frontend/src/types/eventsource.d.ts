declare module 'eventsource' {
    export interface EventSourcePolyfillInit {
      headers?: Record<string, string>;
      withCredentials?: boolean;
      heartbeatTimeout?: number;
    }
  
    export class EventSourcePolyfill extends EventSource {
      constructor(url: string, eventSourceInitDict?: EventSourcePolyfillInit);
    }
}