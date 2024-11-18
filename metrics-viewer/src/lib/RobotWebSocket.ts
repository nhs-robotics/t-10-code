export type PacketType = 'MetricsUpdatePacket' | 'MetricsPointCloudUpdatePacket' | 'MetricsNewConnectionPacket' | 'MetricsCameraFramePacket';

interface Packet {
    _packetType: PacketType;
}

export interface Point {
    x: number;
    y: number;
}

export interface Point3 extends Point {
    z: number;
}

export interface Pose extends Point {
    heading: number;
}

export type MetricType = 'integer' | 'double' | 'string' | 'point' | 'pose';

export interface MetricsUpdatePacket<T extends MetricType> extends Packet {
    metricName: string;
    metricType: T;
    metricValue:
          T extends 'integer' | 'double' ? number
        : T extends 'string' ? string
        : T extends 'point' ? Point
        : T extends 'pose' ? Pose
        : never;
}

export interface MetricsPointCloudUpdatePacket extends Packet {
    points: Point3[];
}

export interface MetricsNewConnectionPacket extends Packet {
    opModeName: string;
    opModeType: 'autonomous' | 'teleOp';
    hardware: {
        name: string;
        type: string;
        connectionDetails: string;
        version: number;
    }[];
}

export interface MetricsCameraFramePacket extends Packet {
    jpegBase64: string;
}

export type PacketHandler<T extends PacketType> =
    (packet: 
          T extends 'MetricsUpdatePacket' ? MetricsUpdatePacket<MetricType>
        : T extends 'MetricsPointCloudUpdatePacket' ? MetricsPointCloudUpdatePacket
        : T extends 'MetricsNewConnectionPacket' ? MetricsNewConnectionPacket
        : T extends 'MetricsCameraFramePacket' ? MetricsCameraFramePacket
        : never
    ) => void;

export class RobotWebSocket {
    private ws: WebSocket | undefined;
    private packetHandlers: Map<PacketType, PacketHandler<PacketType>[]>;
    public isConnected: boolean = false;
    
    constructor() {
        this.packetHandlers = new Map();
        this._connect()
    }

    private _connect() {
        this.ws = new WebSocket("//192.168.43.1:51631");
        // this.ws = new WebSocket('//localhost:8765');
        this.ws.addEventListener('open', () => this._onOpen());
        this.ws.addEventListener('message', e => this._onMessage(e));
        this.ws.addEventListener('close', () => this._onClose());
    }

    private _onOpen() {
        this.isConnected = true;
    }

    private _onMessage(e: MessageEvent) {
        const packet = JSON.parse(e.data) as Packet;
        const handlers = this.packetHandlers.get(packet._packetType);

        if (handlers) {
            handlers.forEach(
                handler => handler(
                    packet as MetricsUpdatePacket<MetricType> | MetricsNewConnectionPacket | MetricsPointCloudUpdatePacket
                )
            );
        }
    }

    private _onClose() {
        this.isConnected = false;
        setTimeout(this._connect, 1000);
    }

    public onPacket<T extends PacketType>(
        packetType: T,
        handler: PacketHandler<T>
    ) {
        const handlers = this.packetHandlers.get(packetType);

        if (handlers === undefined) {
            this.packetHandlers.set(packetType, [handler]);
        } else {
            this.packetHandlers.set(packetType, [...handlers, handler])
        }

        return this;
    }
}
