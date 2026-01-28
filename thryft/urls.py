"""
URL configuration for thryft project.

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/6.0/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from main import views

urlpatterns = [
    path('admin/', admin.site.urls),
    path('', views.home, name='home'), 
    path('products/', views.products, name='products'),
    path('add-to-cart/', views.add_to_cart, name='add_to_cart'),
    path('cart/', views.cart, name='cart'),
    path('login/', views.login_view, name='login'),
    path('register/', views.register_view, name='register'),
    path('logout/', views.logout_view, name='logout'),
    path('dashboard/', views.dashboard, name='dashboard'),
    path('dashboard/profile/', views.profile, name='profile'),
    path('delete-account/', views.delete_account, name='delete_account'),
    path('orders/', views.orders, name='orders'),
    path('logout/', views.logout_view, name='logout'),
    path('cart/update/', views.update_cart, name='update_cart'),
    path('cart/remove/', views.remove_cart_item, name='remove_cart_item'),
    path('product/<int:product_id>/', views.single_product, name='single_product'),
    path('staff/', views.admin_dashboard, name='admin_dashboard'),
    path('staff/products/', views.admin_products, name='admin_products'),
    path('staff/products/edit/<int:product_id>/', views.admin_product_edit, name='admin_product_edit'),
    path('staff/products/add/', views.admin_product_add, name='admin_product_add'),
    path('staff/products/delete/<int:product_id>/', views.admin_product_delete, name='admin_product_delete'),
    path('staff/users/', views.admin_users, name='admin_users'),
    path('staff/users/toggle/<int:user_id>/', views.admin_toggle_staff, name='admin_toggle_staff'),
    path('staff/users/delete/<int:user_id>/', views.admin_user_delete, name='admin_user_delete'),
    path('staff/orders/', views.admin_orders, name='admin_orders'),
    path('staff/orders/assign/<int:order_id>/', views.admin_order_assign, name='admin_order_assign'),
    path('staff/orders/view/<int:order_id>/', views.admin_order_details, name='admin_order_details'),
    path('checkout/', views.process_payment, name='process_payment'),
    path('staff/users/approve/<int:user_id>/', views.admin_approve_user, name='admin_approve_user'),
    path('dashboard/orders/', views.order_history, name='order_history'),
]