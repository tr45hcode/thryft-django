# This is an auto-generated Django model module.
# You'll have to do the following manually to clean this up:
#   * Rearrange models' order
#   * Make sure each model has one field with primary_key=True
#   * Make sure each ForeignKey and OneToOneField has `on_delete` set to the desired behavior
#   * Remove `managed = False` lines if you wish to allow Django to create, modify, and delete the table
# Feel free to rename the models, but don't rename db_table values or field names.
from django.db import models


class AuthGroup(models.Model):
    name = models.CharField(unique=True, max_length=150, blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'auth_group'


class AuthGroupPermissions(models.Model):
    id = models.BigAutoField(primary_key=True)
    group = models.ForeignKey(AuthGroup, models.DO_NOTHING)
    permission = models.ForeignKey('AuthPermission', models.DO_NOTHING)

    class Meta:
        managed = False
        db_table = 'auth_group_permissions'
        unique_together = (('group', 'permission'),)


class AuthPermission(models.Model):
    name = models.CharField(max_length=255, blank=True, null=True)
    content_type = models.ForeignKey('DjangoContentType', models.DO_NOTHING)
    codename = models.CharField(max_length=100, blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'auth_permission'
        unique_together = (('content_type', 'codename'),)


class AuthUser(models.Model):
    password = models.CharField(max_length=128, blank=True, null=True)
    last_login = models.DateTimeField(blank=True, null=True)
    is_superuser = models.BooleanField()
    username = models.CharField(unique=True, max_length=150, blank=True, null=True)
    first_name = models.CharField(max_length=150, blank=True, null=True)
    last_name = models.CharField(max_length=150, blank=True, null=True)
    email = models.CharField(max_length=254, blank=True, null=True)
    is_staff = models.BooleanField()
    is_active = models.BooleanField()
    date_joined = models.DateTimeField()

    class Meta:
        managed = False
        db_table = 'auth_user'


class AuthUserGroups(models.Model):
    id = models.BigAutoField(primary_key=True)
    user = models.ForeignKey(AuthUser, models.DO_NOTHING)
    group = models.ForeignKey(AuthGroup, models.DO_NOTHING)

    class Meta:
        managed = False
        db_table = 'auth_user_groups'
        unique_together = (('user', 'group'),)


class AuthUserUserPermissions(models.Model):
    id = models.BigAutoField(primary_key=True)
    user = models.ForeignKey(AuthUser, models.DO_NOTHING)
    permission = models.ForeignKey(AuthPermission, models.DO_NOTHING)

    class Meta:
        managed = False
        db_table = 'auth_user_user_permissions'
        unique_together = (('user', 'permission'),)


class Cart(models.Model):
    cart_id = models.AutoField(primary_key=True)
    user = models.ForeignKey('UserTable', models.DO_NOTHING)
    product = models.ForeignKey('Product', models.DO_NOTHING)
    quantity = models.FloatField()
    
    class Meta:
        managed = False
        db_table = 'cart'


class Category(models.Model):
    category_id = models.FloatField(primary_key=True)
    category_name = models.CharField(max_length=100)
    gender = models.CharField(max_length=20)

    def __str__(self):
        return self.category_name + "(" + self.gender + ")"

    class Meta:
        managed = False
        db_table = 'category'


class Customer(models.Model):
    customer = models.OneToOneField('UserTable', models.DO_NOTHING, primary_key=True)
    membership_date = models.DateField()
    membership_status = models.CharField(max_length=50)

    class Meta:
        managed = False
        db_table = 'customer'


class DjangoAdminLog(models.Model):
    action_time = models.DateTimeField()
    object_id = models.TextField(blank=True, null=True)
    object_repr = models.CharField(max_length=200, blank=True, null=True)
    action_flag = models.IntegerField()
    change_message = models.TextField(blank=True, null=True)
    content_type = models.ForeignKey('DjangoContentType', models.DO_NOTHING, blank=True, null=True)
    user = models.ForeignKey(AuthUser, models.DO_NOTHING)

    class Meta:
        managed = False
        db_table = 'django_admin_log'


class DjangoContentType(models.Model):
    app_label = models.CharField(max_length=100, blank=True, null=True)
    model = models.CharField(max_length=100, blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'django_content_type'
        unique_together = (('app_label', 'model'),)


class DjangoMigrations(models.Model):
    id = models.BigAutoField(primary_key=True)
    app = models.CharField(max_length=255, blank=True, null=True)
    name = models.CharField(max_length=255, blank=True, null=True)
    applied = models.DateTimeField()

    class Meta:
        managed = False
        db_table = 'django_migrations'


class DjangoSession(models.Model):
    session_key = models.CharField(primary_key=True, max_length=40)
    session_data = models.TextField(blank=True, null=True)
    expire_date = models.DateTimeField()

    class Meta:
        managed = False
        db_table = 'django_session'








class Product(models.Model):
    product_id = models.FloatField(primary_key=True)
    category = models.ForeignKey(Category, models.DO_NOTHING)
    name = models.CharField(max_length=100)
    description = models.TextField(blank=True, null=True)
    price = models.DecimalField(max_digits=10, decimal_places=2)
    stock = models.FloatField()
    image_path = models.CharField(max_length=500, blank=True, null=True)

    def __str__(self):
        return self.name

    class Meta:
        managed = False
        db_table = 'product'


class Staff(models.Model):
    # This links to your UserTable.
    staff_id = models.OneToOneField(
        'UserTable', 
        on_delete=models.DO_NOTHING, 
        primary_key=True, 
        db_column='staff_id'
    )
    # Supervisor links to another Staff member
    supervisor = models.ForeignKey(
        'self', 
        models.DO_NOTHING, 
        blank=True, 
        null=True, 
        related_name='team_members', # Moved inside the field
        db_column='supervisor_id'    # Ensure this matches your Oracle column name
    )
    job_title = models.CharField(max_length=100)
    employment_type = models.CharField(max_length=50)
    department = models.CharField(max_length=100)
    hire_date = models.DateField()
    hourly_rate = models.DecimalField(max_digits=10, decimal_places=2)
    shift = models.CharField(max_length=50, blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'staff' # Oracle table name


class UserTable(models.Model):
    user_id = models.AutoField(primary_key=True)
    name = models.CharField(max_length=100)
    email = models.EmailField(unique=True, max_length=100)
    password = models.CharField(max_length=255)
    phone = models.CharField(max_length=20, blank=True, null=True)
    address = models.CharField(max_length=500, blank=True, null=True)
    role = models.CharField(max_length=20, default='pending')

    class Meta:
        managed = False
        db_table = 'user_table'

class OrderTable(models.Model):
    order_id = models.AutoField(primary_key=True)
    customer = models.ForeignKey(Customer, models.DO_NOTHING, db_column='customer_id')
    total_amount = models.DecimalField(max_digits=10, decimal_places=2)
    order_date = models.DateTimeField(auto_now_add=True)
    staff = models.ForeignKey(
        'Staff', 
        models.DO_NOTHING, 
        db_column='staff_id', 
        null=True, 
        blank=True
    )

    class Meta:
        managed = False
        db_table = 'order_table'

class OrderItem(models.Model):
    order_item_id = models.AutoField(primary_key=True)
    order = models.ForeignKey(OrderTable, models.DO_NOTHING, db_column='order_id')
    product = models.ForeignKey('Product', models.DO_NOTHING, db_column='product_id')
    
    # Quantity should be Integer, not Float
    quantity = models.IntegerField()
    subtotal = models.DecimalField(max_digits=10, decimal_places=2)

    class Meta:
        managed = False
        db_table = 'order_item'

class Payment(models.Model):
    payment_id = models.BigAutoField(primary_key=True)
    order = models.ForeignKey(OrderTable, models.DO_NOTHING)
    payment_method = models.CharField(max_length=50)
    payment_date = models.DateTimeField()
    amount = models.DecimalField(max_digits=10, decimal_places=2)

    class Meta:
        managed = False
        db_table = 'payment'