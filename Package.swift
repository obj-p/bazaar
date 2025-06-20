// swift-tools-version: 6.1

import PackageDescription

let package = Package(
    name: "bazaar",
    products: [
        .library(
            name: "bazaar",
            targets: ["bazaar"]
        )
    ],
    targets: [
        .target(
            name: "bazaar"),
        .testTarget(
            name: "bazaarTests",
            dependencies: ["bazaar"]
        ),
        .target(
            name: "Templates")
    ]
)
