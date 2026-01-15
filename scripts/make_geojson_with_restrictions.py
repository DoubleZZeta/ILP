import json
import sys
from pathlib import Path


def load_json(path: str | Path) -> dict | list:
    """Load JSON from a file."""
    with open(path, "r", encoding="utf-8") as f:
        return json.load(f)


def combine_drone_paths(drone_path: dict) -> list[list[float]]:
    """
    Given one dronePath object from the ReturnedPath JSON,
    combine all deliveries' flightPaths into a single list of [lng, lat] coords.
    """
    coords: list[list[float]] = []

    for delivery in drone_path.get("deliveries", []):
        for pos in delivery.get("flightPath", []):
            lng = pos["lng"]
            lat = pos["lat"]
            point = [lng, lat]

            # Avoid consecutive duplicates (hover or stitched segments)
            if not coords or coords[-1] != point:
                coords.append(point)

    return coords


def returned_path_to_features(returned_path: dict) -> list[dict]:
    """
    Convert ReturnedPath JSON (with dronePaths/deliveries/flightPath)
    into a list of GeoJSON Features (one LineString per drone).
    """
    features: list[dict] = []

    for drone_path in returned_path.get("dronePaths", []):
        drone_id = drone_path.get("droneId") or drone_path.get("dronId")

        coords = combine_drone_paths(drone_path)
        if not coords:
            continue  # no path for this drone

        feature = {
            "type": "Feature",
            "properties": {
                "droneId": drone_id,
            },
            "geometry": {
                "type": "LineString",
                "coordinates": coords,
            },
        }
        features.append(feature)

    return features


def restrictions_to_features(restricted_areas: list[dict]) -> list[dict]:
    """
    Convert your restrictedAreas JSON into a list of GeoJSON Polygon Features.
    """
    features: list[dict] = []

    for area in restricted_areas:
        name = area.get("name")
        area_id = area.get("id")
        limits = area.get("limits", {})

        # Build the polygon ring: [[lng, lat], ...]
        ring = [[v["lng"], v["lat"]] for v in area.get("vertices", [])]

        # Ensure polygon is closed: first coord == last coord
        if ring and ring[0] != ring[-1]:
            ring.append(ring[0])

        feature = {
            "type": "Feature",
            "properties": {
                "name": name,
                "id": area_id,
                "lower": limits.get("lower"),
                "upper": limits.get("upper"),
                "kind": "restrictedArea",
            },
            "geometry": {
                "type": "Polygon",
                "coordinates": [ring],
            },
        }
        features.append(feature)

    return features


def main():
    if len(sys.argv) < 4:
        print(
            "Usage: python make_geojson_with_restrictions.py "
            "<returned_path.json> <restricted_areas.json> <output.geojson>"
        )
        sys.exit(1)

    returned_path_file = Path(sys.argv[1])
    restricted_areas_file = Path(sys.argv[2])
    output_file = Path(sys.argv[3])

    # 1. Load inputs
    returned_path = load_json(returned_path_file)
    restricted_areas = load_json(restricted_areas_file)

    # 2. Convert each to features
    path_features = returned_path_to_features(returned_path)
    restriction_features = restrictions_to_features(restricted_areas)

    # 3. Combine into one FeatureCollection
    combined = {
        "type": "FeatureCollection",
        "features": path_features + restriction_features,
    }

    # 4. Write output
    output_file.write_text(json.dumps(combined, indent=2), encoding="utf-8")
    print(f"Wrote combined GeoJSON to {output_file}")


if __name__ == "__main__":
    main()
