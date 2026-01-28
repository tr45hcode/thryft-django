# Register your models here.

from django.contrib import admin
from .models import UserTable, Product, Category, OrderTable

admin.site.register(UserTable)
admin.site.register(Product)
admin.site.register(Category)
admin.site.register(OrderTable)