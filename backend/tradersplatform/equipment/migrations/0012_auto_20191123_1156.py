# Generated by Django 2.2.6 on 2019-11-23 11:56

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('equipment', '0011_auto_20191123_1156'),
    ]

    operations = [
        migrations.AlterField(
            model_name='stocks',
            name='AAPL',
            field=models.DecimalField(blank=True, decimal_places=10, max_digits=10, null=True),
        ),
        migrations.AlterField(
            model_name='stocks',
            name='GM',
            field=models.DecimalField(blank=True, decimal_places=10, max_digits=10, null=True),
        ),
        migrations.AlterField(
            model_name='stocks',
            name='GOOGL',
            field=models.DecimalField(blank=True, decimal_places=10, max_digits=10, null=True),
        ),
    ]