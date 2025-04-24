interface EventSourceEvent {
    data: string;
    type?: string;
    lastEventId?: string;
    origin?: string;
}