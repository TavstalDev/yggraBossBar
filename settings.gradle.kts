rootProject.name = "yggraBossBar"

include(
    "api",
    "impl_v1_8_8",
    "impl_v1_9_4",
    "impl_v1_13",
    "impl_v1_16_5",
    "bootstrap",
    "plugin-test"
)

project(":impl_v1_8_8").projectDir = file("implementation/impl_v1_8_8")
project(":impl_v1_9_4").projectDir = file("implementation/impl_v1_9_4")
project(":impl_v1_13").projectDir = file("implementation/impl_v1_13")
project(":impl_v1_16_5").projectDir = file("implementation/impl_v1_16_5")