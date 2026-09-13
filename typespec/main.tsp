import "@typespec/http";
import "@typespec/rest";
import "@typespec/openapi3";

using TypeSpec.Http;
using TypeSpec.Rest;

/**
 * Food Service API
 *
 * Domain models, request DTOs, persistence shapes, and catalog endpoints
 * in one place. Mirrors the Kotlin service in src/main/kotlin.
 */
@service(#{
  title: "Food Service",
})
@server("http://localhost:8080", "Local development")
namespace FoodService;

/** RFC 4122 UUID string. */
@format("uuid")
scalar uuid extends string;

// ---------------------------------------------------------------------------
// Domain
// ---------------------------------------------------------------------------

/** Base type for identifiable objects. */
model Object {
  /** Server-generated identifier. */
  uuid: uuid;
}

/** A food item returned by the API. */
model Food extends Object {
  /** Identifier reserved for an associated image (future use). */
  imageId: uuid;
  name: string;
  /** Subjective rating from 1 (worst) to 5 (best). */
  @minValue(1)
  @maxValue(5)
  tasteRating: int32;
}

/** A food currently stocked in the food inventory. */
model InventoryFood extends Food {
  /** Number of units currently in inventory. */
  @minValue(1)
  quantity: int32;
}

// ---------------------------------------------------------------------------
// Request DTOs
// ---------------------------------------------------------------------------

/** Body for POST /catalog/entries. Server generates uuid and imageId if omitted. */
model CreateFoodRequest {
  name: string;
  @minValue(1)
  @maxValue(5)
  tasteRating: int32;
  /** Optional. Server generates one when omitted. */
  imageId?: uuid;
}

/** Body for PUT /catalog/entries/{uuid}. Keeps existing imageId when omitted. */
model UpdateFoodRequest {
  name: string;
  @minValue(1)
  @maxValue(5)
  tasteRating: int32;
  /** Optional. Existing imageId is kept when omitted. */
  imageId?: uuid;
}

// ---------------------------------------------------------------------------
// Persistence (MongoDB — not exposed directly on the API)
// ---------------------------------------------------------------------------

/** Stored in the `food_catalog` collection. Maps to Food via FoodCatalogDocument.toFood(). */
model FoodCatalogDocument {
  @key
  uuid: uuid;
  imageId: uuid;
  name: string;
  @minValue(1)
  @maxValue(5)
  tasteRating: int32;
}

/** Stored in the legacy `snack_inventory` collection for data compatibility. */
model InventoryFoodDocument extends FoodCatalogDocument {
  @minValue(1)
  quantity: int32;
}

// ---------------------------------------------------------------------------
// Errors
// ---------------------------------------------------------------------------

@error
model NotFoundError {
  @statusCode status: 404;
  message: string;
}

model NoContent {
  @statusCode status: 204;
}

// ---------------------------------------------------------------------------
// API
// ---------------------------------------------------------------------------

@tag("Catalog Entries")
@route("/catalog/entries")
interface CatalogEntries {
  /** Add a food to the catalog. */
  @post
  addEntry(@body request: CreateFoodRequest): Food;

  /** List all catalog entries. */
  @get
  getAllEntries(): Food[];

  /** Update an existing catalog entry. */
  @put
  @route("/{uuid}")
  updateEntry(
    @path uuid: uuid,
    @body request: UpdateFoodRequest,
  ): Food | NotFoundError;

  /** Remove a catalog entry. */
  @delete
  @route("/{uuid}")
  deleteEntry(@path uuid: uuid): NoContent | NotFoundError;
}

@tag("Food Inventory")
@route("/inventory/foods")
interface FoodInventory {
  /** List all foods currently in inventory. */
  @get
  getAllFoods(): InventoryFood[];

  /** Get one inventory food by its catalog food UUID. */
  @get
  @route("/{uuid}")
  getFood(@path uuid: uuid): InventoryFood | NotFoundError;

  /**
   * Add one unit of a catalog food to inventory.
   * Creates the entry with quantity 1 when it is not yet stocked.
   */
  @post
  @route("/{uuid}")
  addFood(@path uuid: uuid): InventoryFood | NotFoundError;

  /**
   * Remove one unit from inventory.
   * Deletes the entry when its quantity reaches zero.
   */
  @delete
  @route("/{uuid}")
  removeFood(@path uuid: uuid): NoContent | NotFoundError;
}
