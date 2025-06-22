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
    dependencies: [
        .package(url: "https://github.com/antlr/antlr4", exact: "4.13.1")
    ],
    targets: [
        .target(
            name: "bazaar"),
        .testTarget(
            name: "bazaarTests",
            dependencies: ["bazaar"]
        ),
        .target(
            name: "Template",
            dependencies: [.product(name: "Antlr4", package: "antlr4")]
        ),
        .testTarget(
            name: "TemplateTests",
            dependencies: ["Template"]
        )
    ]
)
