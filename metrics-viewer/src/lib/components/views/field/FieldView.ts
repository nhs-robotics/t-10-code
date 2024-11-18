import type { Group, Sprite } from "three/webgpu";

export interface MarkerObject {
    group: Group;
    label: Sprite;
}

// Store for marker objects
export const markerObjects = new Map<string, MarkerObject>();

export function setMarkerVisible(metricName: string, visible: boolean): void {
    const marker = markerObjects.get(metricName);

    if (marker) {
        marker.group.visible = visible;
        marker.label.visible = visible;
    }
}

export function getMarketVisible(metricName: string): boolean {
    const marker = markerObjects.get(metricName);

    if (!marker) {
        return false;
    }

    return marker.group.visible && marker.label.visible;
}