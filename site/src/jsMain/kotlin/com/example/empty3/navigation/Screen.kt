package com.example.empty3.navigation

sealed class Screen(val route: String) {
    object HomePage : Screen(route = "/")
    object ProductListPage : Screen(route = "/products")
    object FoodPage : Screen(route = "/foods")
    object DrinkPage : Screen(route = "/drinks")
    object ProductUpdatePage : Screen(route = "/updateProduct")
    object RegisterPage : Screen(route = "/register")
    object LoginPage : Screen(route = "/login")
    object UserReviewsPage : Screen(route = "/user/reviews")
    data class ProductDetailPage(val id: String) : Screen(route = "/product/$id")
    data class CategoryPage(val category: String) : Screen(route = "/category/$category")
    data class SearchResultsPage(val query: String) : Screen(route = "/search/$query")
}


