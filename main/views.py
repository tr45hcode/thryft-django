
from django.http import HttpResponse
from django.shortcuts import render,redirect,get_object_or_404
from .models import Product, Category,UserTable as User,Cart,OrderTable,OrderItem, Payment, Customer, Staff
from django.contrib.auth import logout
from django.contrib import messages
from django.conf import settings
from django.core.files.storage import FileSystemStorage
from django.utils import timezone
from django.db import transaction
import os

def home(request):
    # Pulling from your Oracle 'product' table
    men_products = Product.objects.filter(category__gender='Male')
    women_products = Product.objects.filter(category__gender='Female')

    print(f"Query Count: {men_products.count()}")
    print(f"Actual Data: {list(men_products)}")
    
    context = {
        'men_products': men_products,
        'women_products': women_products
    }
    return render(request, 'main/index.html', context)

def products(request):
    return render(request, 'main/products.html')

def cart(request):
    return render(request, 'main/cart.html')

def dashboard(request):
    return render(request, 'main/dashboard.html')

def login_view(request):
    if request.method == "POST":
        u_name = request.POST.get('username')
        p_word = request.POST.get('password')

        try:
            # Checking against your Oracle user_table
            user = User.objects.get(name=u_name, password=p_word)
            if user.role == 'pending':
                messages.error(request, "Your account is awaiting admin approval.")
                return redirect('login')
            
            # For now, we manually set a session (Standard Django auth works differently)
            request.session['user_id'] = user.user_id
            request.session['username'] = user.name
            return redirect('home')
            
        except User.DoesNotExist:
            return render(request, 'main/login.html', {'error': 'Invalid Username or Password'})

    return render(request, 'main/login.html')

def register_view(request):
    if request.method == "POST":
        u_name = request.POST.get('username')
        email = request.POST.get('email')
        passw = request.POST.get('password')
        confirm = request.POST.get('confirm_password')

        # 1. Basic Validation
        if passw != confirm:
            return render(request, 'main/register.html', {'error': 'Passwords do not match!'})

        # 2. Check if email already exists
        if User.objects.filter(email=email).exists():
            return render(request, 'main/register.html', {'error': 'Email already registered!'})

        # 3. Save to Oracle
        # Since managed=False, ensure your Oracle ID is set to Identity or Auto-increment
        new_user = User(
            name=u_name,
            email=email,
            password=passw # Note: In a real app, use make_password(passw) for security
        )
        new_user.save()

        return redirect('login')

    return render(request, 'main/register.html')

def logout_view(request):
    # This clears the specific keys you set
    try:
        del request.session['user_id']
        del request.session['username']
    except KeyError:
        pass

    # Or completely flush the session
    request.session.flush()
    
    return redirect('home')

def dashboard(request):
    # Security Check: If 'user_id' is not in session, kick them to login
    if 'user_id' not in request.session:
        return redirect('login')
    
    return render(request, 'main/dashboard.html')

def orders(request):
    # Placeholder for Orders page
    if 'user_id' not in request.session:
        return redirect('login')
    return render(request, 'main/orders.html') # You will need to create this later

def profile(request):
    # 1. Check if user is logged in
    user_id = request.session.get('user_id')
    if not user_id:
        return redirect('login')

    # 2. Get the User object from Oracle
    user = get_object_or_404(User, user_id=user_id)
    context = {'user': user}

    if request.method == "POST":
        # Get form data
        new_username = request.POST.get('username')
        new_email = request.POST.get('email')
        new_address = request.POST.get('address')
        current_password = request.POST.get('currentPassword')
        new_password = request.POST.get('newPassword')
        new_phone = request.POST.get('phone')

        # 3. Security Check: Must enter current password to update
        if current_password != user.password:
            context['error'] = "Incorrect current password!"
            return render(request, 'main/profile.html', context)

        # 4. Update fields
        user.name = new_username
        user.email = new_email
        user.phone = new_phone
        user.address = new_address

        # Only update password if a new one was entered
        if new_password:
            user.password = new_password

        # Save to Oracle DB
        user.save()

        # Update session if username changed (so the dashboard greets the new name)
        request.session['username'] = user.name

        context['success'] = "Profile updated successfully!"
        return render(request, 'main/profile.html', context)

    # GET Request: Just show the form
    return render(request, 'main/profile.html', context)

def delete_account(request):
    if request.method == "POST":
        user_id = request.session.get('user_id')
        if user_id:
            # Find and delete user
            try:
                user = User.objects.get(user_id=user_id)
                user.delete()
            except User.DoesNotExist:
                pass
            
            # Clear session
            request.session.flush()
            
    return redirect('home')

def cart(request):
    # 1. Check Login
    user_id = request.session.get('user_id')
    if not user_id:
        return redirect('login')

    # 2. Get User Address
    user = get_object_or_404(User, user_id=user_id)
    user_address = user.address

    # 3. Fetch Cart Items for this user
    # Note: select_related('product') optimizes the SQL query to fetch product details in one go
    cart_items = Cart.objects.filter(user_id=user_id).select_related('product')

    # 4. Calculate Grand Total
    grand_total = 0
    for item in cart_items:
        # Calculate item total temporarily for display
        item.total_price = item.quantity * item.product.price
        grand_total += item.total_price

    context = {
        'cart_items': cart_items,
        'grand_total': grand_total,
        'user_address': user_address
    }
    return render(request, 'main/cart.html', context)

def update_cart(request):
    if request.method == "POST":
        user_id = request.session.get('user_id')
        product_id = request.POST.get('product_id')
        action = request.POST.get('action')

        if user_id and product_id:
            # Get the specific cart item
            item = get_object_or_404(Cart, user_id=user_id, product_id=product_id)

            if action == 'increment':
                item.quantity += 1
                item.save()
            elif action == 'decrement':
                if item.quantity > 1:
                    item.quantity -= 1
                    item.save()
                else:
                    # If quantity is 1 and user clicks "-", remove item
                    item.delete()

    return redirect('cart')

def remove_cart_item(request):
    if request.method == "POST":
        user_id = request.session.get('user_id')
        product_id = request.POST.get('product_id')

        if user_id and product_id:
            Cart.objects.filter(user_id=user_id, product_id=product_id).delete()

    return redirect('cart')

def products(request):
    # Fetch all products from Oracle
    all_products = Product.objects.all()
    
    context = {
        'all_products': all_products
    }
    return render(request, 'main/products.html', context)

def add_to_cart(request):
    if request.method == "POST":
        # 1. Check if user is logged in
        user_id = request.session.get('user_id')
        if not user_id:
            return redirect('login')

        # 2. Get form data
        product_id = request.POST.get('product_id')
        quantity = int(request.POST.get('quantity', 1))

        # 3. Get objects
        product = get_object_or_404(Product, product_id=product_id)
        user = get_object_or_404(User, user_id=user_id)

        # 4. Check if item already exists in cart
        cart_item, created = Cart.objects.get_or_create(
            user=user,
            product=product,
            defaults={'quantity': quantity}
        )

        if not created:
            # If item exists, just update quantity
            cart_item.quantity += quantity
            cart_item.save()

        # 5. Show success message (Toast)
        messages.success(request, f"Added {product.name} to cart!")

        # Redirect back to the products page
        return redirect('products')

    return redirect('products')

def single_product(request, product_id):
    # This fetches the specific product or returns a 404 error if not found
    product = get_object_or_404(Product, product_id=product_id)
    
    context = {
        'product': product
    }
    return render(request, 'main/single-product.html', context)

def admin_check(request):
    """
    Validates if the user is logged in and has the 'staff' role in Oracle.
    """
    user_id = request.session.get('user_id')
    if not user_id:
        return False
    try:
        user = User.objects.get(user_id=user_id)
        return user.role == 'staff'
    except User.DoesNotExist:
        return False

# --- Protected Views ---
def admin_dashboard(request):
    # Security Measure: Redirect unauthorized users to home
    if not admin_check(request):
        return redirect('home')
    
    return render(request, 'main/admin.html')

# Example of protecting another view using the same measure
def admin_products(request):
    if not admin_check(request):
        return redirect('home')
    
    products = Product.objects.all()
    return render(request, 'main/productList.html', {'products': products})

def admin_product_edit(request, product_id):
    if not admin_check(request):
        return redirect('home')

    product = get_object_or_404(Product, product_id=product_id)
    categories = Category.objects.all()

    if request.method == "POST":
        product.name = request.POST.get('name')
        product.price = request.POST.get('price')
        product.stock = request.POST.get('stocks')
        product.category_id = request.POST.get('category_id')

        # HANDLE IMAGE UPLOAD
        new_image = request.FILES.get('product_image')
        if new_image:
            # Path to your project's static images folder
            upload_path = os.path.join(settings.BASE_DIR, 'main', 'static', 'main', 'assets', 'images')
            
            # Save the file physically
            fs = FileSystemStorage(location=upload_path)
            filename = fs.save(new_image.name, new_image)
            print(f"DEBUG: Saving image to {filename}")
            
            # Update the database field with just the filename
            product.image_path = filename

        product.save()
        return redirect('admin_products')

    return render(request, 'main/editProduct.html', {
        'product': product,
        'categories': categories
    })

def admin_product_add(request):
    if not admin_check(request):
        return redirect('home')

    categories = Category.objects.all()

    if request.method == "POST":
        try:
            # 1. Handle Image
            image_file = request.FILES.get('product_image')
            if image_file:
                # Targeted directory
                upload_path = os.path.join(settings.BASE_DIR, 'main', 'static', 'main', 'assets', 'images')
                
                # Ensure the folder exists
                if not os.path.exists(upload_path):
                    os.makedirs(upload_path)
                
                fs = FileSystemStorage(location=upload_path)
                filename = fs.save(image_file.name, image_file)
            else:
                filename = 'default.jpg'

            # 2. Save to Oracle
            Product.objects.create(
                name=request.POST.get('name'),
                price=request.POST.get('price'),
                stock=request.POST.get('stocks'), # Capture new stocks field
                category_id=request.POST.get('category_id'),
                image_path=filename,
                description=request.POST.get('description')
            )
            
            return redirect('admin_products')
        except Exception as e:
            messages.error(request, f"Error: {str(e)}")

    return render(request, 'main/addProduct.html', {'categories': categories})

def admin_product_delete(request, product_id):
    # 1. Security Check
    if not admin_check(request):
        return redirect('home')

    # 2. Only allow deletion via POST for security
    if request.method == "POST":
        try:
            # Find the product in Oracle and delete it
            product = get_object_or_404(Product, product_id=product_id)
            product_name = product.name
            product.delete()
            
            messages.success(request, f"Product '{product_name}' has been deleted.")
        except Exception as e:
            messages.error(request, f"Error deleting product: {str(e)}")
            
    return redirect('admin_products')


# 1. List Users (Read)
def admin_users(request):
    if not admin_check(request): # Security helper we made earlier
        return redirect('home')
    
    # Get everyone in the user_table
    users = User.objects.all().order_by('role', 'user_id')
    return render(request, 'main/admin_users.html', {'users': users})

# 2. Toggle Staff Status (Update)
def admin_toggle_staff(request, user_id):
    if not admin_check(request): 
        return redirect('home')

    if request.method == "POST":
        user_to_change = get_object_or_404(User, user_id=user_id)
        
        # Don't let staff demote themselves
        if user_to_change.user_id == request.session.get('user_id'):
            messages.error(request, "You cannot change your own role.")
            return redirect('admin_users')

        if user_to_change.role == 'staff':
            user_to_change.role = 'customer'
            messages.success(request, f"{user_to_change.name} is now a Customer.")
        else:
            user_to_change.role = 'staff'
            messages.success(request, f"{user_to_change.name} promoted to Staff.")
            
        user_to_change.save()
    return redirect('admin_users')

def admin_user_delete(request, user_id):
    if not admin_check(request): 
        return redirect('home')

    if request.method == "POST":
        user_to_delete = get_object_or_404(User, user_id=user_id)
        
        # 1. Check if user is trying to delete themselves
        if user_to_delete.user_id == request.session.get('user_id'):
            messages.error(request, "You cannot delete your own account.")
            return redirect('admin_users')

        try:
            # 2. DELETE CHILD RECORDS FIRST
            # Delete items in the Cart table belonging to this user
            Cart.objects.filter(user_id=user_id).delete()
            
            # If you have a Customer table record, delete that too
            Customer.objects.filter(customer_id=user_id).delete()

            # 3. NOW DELETE THE USER
            user_to_delete.delete()
            messages.success(request, "User and their related data deleted successfully.")
            
        except Exception as e:
            messages.error(request, f"Error during deletion: {str(e)}")
            
    return redirect('admin_users')

def admin_orders(request):
    if not admin_check(request):
        return redirect('home')
        
    # select_related fetches customer and staff details in one SQL query
    orders = OrderTable.objects.all().select_related('customer','staff').order_by('-order_date')
    return render(request, 'main/admin_orders.html', {'orders': orders})

# 2. Assign an order to the current staff member
def admin_order_assign(request, order_id):
    # 1. Get current logged-in user ID
    current_user_id = request.session.get('user_id')
    print(current_user_id)
    
    # 2. Get the Staff record (ORA-02291 protection)
    staff_member = get_object_or_404(Staff, staff_id=current_user_id)
    print(staff_member)
    
    # 3. Get the Order
    order = get_object_or_404(OrderTable, order_id=order_id)
    
    # 4. Save
    order.staff = staff_member
    order.save()
    
    messages.success(request, f"Order #{order_id} assigned to you.")
    return redirect('admin_order_details', order_id=order_id)

# 3. View specific items in an order
def admin_order_details(request, order_id):
    if not admin_check(request):
        return redirect('home')
        
    order = get_object_or_404(OrderTable, order_id=order_id)
    items = OrderItem.objects.filter(order=order).select_related('product')
    
    return render(request, 'main/admin_order_details.html', {
        'order': order,
        'items': items
    })

def admin_approve_user(request, user_id):
    if not admin_check(request):
        return redirect('home')

    if request.method == "POST":
        user_to_approve = get_object_or_404(User, user_id=user_id)
        chosen_role = request.POST.get('role')

        if chosen_role == 'customer':
            user_to_approve.role = 'customer'
            Customer.objects.get_or_create(
                customer_id=user_to_approve.user_id,
                defaults={'membership_status': 'active'}
            )
        
        elif chosen_role == 'staff':
            user_to_approve.role = 'staff'
            # Create the record in the STAFF table to satisfy Foreign Key constraints
            Staff.objects.get_or_create(
                staff_id=user_to_approve, # Links to UserTable
                defaults={
                    'job_title': 'Junior Staff',
                    'employment_type': 'Full-time',
                    'department': 'General',
                    'hire_date': timezone.now().date(),
                    'hourly_rate': 15.00
                }
            )
            messages.success(request, f"{user_to_approve.name} approved as Staff.")

        user_to_approve.save()
        
    return redirect('admin_users')

def process_payment(request):
    user_id = request.session.get('user_id')
    
    # 1. Fetch the base User record
    try:
        user_record = User.objects.get(user_id=user_id)
    except User.DoesNotExist:
        messages.error(request, "User session invalid. Please log in again.")
        return redirect('login')

    if request.method == "POST":
        # 2. Get Cart items
        cart_items = Cart.objects.filter(user_id=user_id)
        if not cart_items:
            messages.error(request, "Your cart is empty.")
            return redirect('cart')

        # 3. Ensure record exists in CUSTOMER_TABLE (Fixes ORA-02291)
        # Note: 'customer_id' is used as the link field per your previous error
        customer_record, created = Customer.objects.get_or_create(
            customer_id=user_record.user_id,
            defaults={
                'membership_status': 'active',
                'membership_date': timezone.now()
            }
        )

        # 4. Calculate Total
        total = sum(item.product.price * item.quantity for item in cart_items)

        try:
            # Use atomic transaction: if one step fails, Oracle rolls everything back
            with transaction.atomic():
                # 5. Create the Order (Linking to the CUSTOMER record)
                order = OrderTable.objects.create(
                    customer=customer_record, 
                    total_amount=total
                    # staff_id remains NULL (allowed per our ALTER TABLE)
                )

                # 6. Move items from Cart to OrderItems & Update Stocks
                for item in cart_items:
                    OrderItem.objects.create(
                        order=order,
                        product=item.product,
                        quantity=item.quantity,
                        subtotal=item.product.price * item.quantity
                    )
                    
                    # Reduce stock (Ensure field is 'stocks' or 'stock' per your model)
                    item.product.stock -= item.quantity 
                    item.product.save()

                # 7. Record the Payment (Hardcoded to skip manual selection)
                current_time_str = timezone.now().isoformat()
                Payment.objects.create(
                    order=order,
                    payment_method="Direct Checkout (Test)", 
                    amount=total, # Use 'amount_paid' to match your model
                    payment_date = current_time_str
                )

                # 8. Clear the Cart
                cart_items.delete()

                return render(request, 'main/payment_success.html', {'order_id': order.order_id})

        except Exception as e:
            print(f"Database Error: {e}") 
            messages.error(request, f"Failed to complete transaction: {str(e)}")
            return redirect('cart')

    return redirect('cart')

def order_history(request):
    user_id = request.session.get('user_id')
    if not user_id:
        return redirect('login')

    # Fetch orders belonging to this customer
    # We use select_related for the staff and prefetch_related for items
    orders = OrderTable.objects.filter(customer_id=user_id).order_by('-order_date')

    return render(request, 'main/order_history.html', {'orders': orders})

def admin_order_details(request, order_id):
    if not admin_check(request):
        return redirect('home')
        
    # Get the order and related customer/user info
    order = get_object_or_404(OrderTable.objects.select_related('customer__customer', 'staff'), order_id=order_id)
    
    # Get all items in this order
    items = OrderItem.objects.filter(order=order).select_related('product')
    
    # Get the payment record
    payment = Payment.objects.filter(order=order).first()

    if order.customer:
        # This prints the Name from the UserTable linked to the Customer record
        print(f"Customer Name: {order.customer.customer.name}")
        print(f"Customer Email: {order.customer.customer.address}")
    else:
        print("No customer associated with this order.")
    
    return render(request, 'main/admin_order_details.html', {
        'order': order,
        'items': items,
        'payment': payment
    })